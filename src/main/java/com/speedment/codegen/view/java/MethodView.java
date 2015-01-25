/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.codegen.view.java;

import com.speedment.codegen.CodeGenerator;
import com.speedment.codegen.model.method.Method_;
import com.speedment.codegen.view.CodeView;
import static com.speedment.codegen.CodeUtil.*;
import java.util.stream.Collectors;
import com.speedment.util.$;
import java.util.Optional;

/**
 *
 * @author Duncan
 */
public class MethodView extends CodeView<Method_> {
	@Override
	public Optional<CharSequence> render(CodeGenerator cg, Method_ method) {
		return Optional.of(new $(
			cg.onEach(method.getAnnotations())
				.collect(Collectors.joining(nl(), EMPTY, nl())),
			cg.on(method.getType()).get(), SPACE,
			method.getName(), PS,
				cg.onEach(method.getParameters())
					.collect(Collectors.joining(COMMA_SPACE)),
			PE, SPACE, looseBracketsIndent(
				cg.onEach(method.getStatements())
					.collect(Collectors.joining(nl()))
			)
		));
	}
}