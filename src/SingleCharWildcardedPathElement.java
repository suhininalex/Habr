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

/**
 * A literal path element that does includes the single character wildcard '?' one
 * or more times (to basically many any character at that position).
 *
 * @author Andy Clement
 * @since 5.0
 */
class SingleCharWildcardedPathElement extends LiteralPathElement {

	private int questionMarkCount;

	public SingleCharWildcardedPathElement(
			int pos, char[] literalText, int questionMarkCount, boolean caseSensitive, char separator)     {
		super(pos, literalText, caseSensitive, separator);
		this.questionMarkCount = questionMarkCount;
	}

	@Override
	protected boolean matches(char data, char current) {
		return current == '?' || super.matches(data, current);
	}

	@Override
	public int getWildcardCount() {
		return this.questionMarkCount;
	}


	public String toString() {
		return "SingleCharWildcarded(" + String.valueOf(this.text) + ")";
	}
}
