/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.springframework.http.server.reactive.PathContainer.Element;
import org.springframework.http.server.reactive.PathContainer.Segment;
import org.springframework.web.util.pattern.PathPattern.MatchingContext;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A literal path element. In the pattern '/foo/bar/goo' there are three
 * literal path elements 'foo', 'bar' and 'goo'.
 *
 * @author Andy Clement
 */
class LiteralPathElement extends PathElement {

	protected char[] text;

	protected int len;

	protected boolean caseSensitive;

	protected boolean matches(char data, char current){
		return data == current;
	}

	public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive, char separator)     {
		super(pos, separator);
		this.len = literalText.length;
		this.caseSensitive = caseSensitive;
		if (caseSensitive) {
			this.text = literalText;
		}
		else {
			// Force all the text lower case to make matching faster
			this.text = new char[literalText.length];
			for (int i = 0; i < len; i++) {
				this.text[i] = Character.toLowerCase(literalText[i]);
			}
		}
	}

	@Override
	public boolean matches(int pathIndex, MatchingContext matchingContext) {
		if (pathIndex >= matchingContext.pathLength) {
			// no more path left to match this element
			return false;
		}
		Element element = matchingContext.pathElements.get(pathIndex);
		if (!(element instanceof Segment)) {
			return false;
		}
		String value = ((Segment)element).valueDecoded();
		if (value.length() != len) {
			// Not enough data to match this path element
			return false;
		}

		char[] data = ((Segment)element).valueDecodedChars();
		if (this.caseSensitive) {
			for (int i = 0; i < len; i++) {
				if (! matches(data[i], this.text[i]))	return false;
			}
		}
		else {
			for (int i = 0; i < len; i++) {
				// TODO revisit performance if doing a lot of case insensitive matching
				if (! matches(Character.toLowerCase(data[i]), text[i])) return false;
			}
		}

		pathIndex++;
		if (isNoMorePattern()) {
			if (matchingContext.determineRemainingPath) {
				matchingContext.remainingPathIndex = pathIndex;
				return true;
			}
			else {
				if (pathIndex == matchingContext.pathLength) {
					return true;
				}
				else {
					return (matchingContext.isAllowOptionalTrailingSlash() &&
							(pathIndex + 1) == matchingContext.pathLength &&
							matchingContext.isSeparator(pathIndex));
				}
			}
		}
		else {
			if (matchingContext.isMatchStartMatching && pathIndex == matchingContext.pathLength) {
				return true;  // no more data but everything matched so far
			}
			return this.next.matches(pathIndex, matchingContext);
		}
	}

	@Override
	public int getNormalizedLength() {
		return len;
	}


	public String toString() {
		return "Literal(" + String.valueOf(this.text) + ")";
	}
	
	public char[] getChars() {
		return this.text;
	}

}
