/*
 * Copyright 2013 juancavallotti.
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

package org.jdto.tools.testobjects;

import org.jdto.annotation.DTOVerify;
import org.jdto.annotation.Source;

/**
 *
 * @author juancavallotti
 */
@DTOVerify(sourceBean=NestedEntity.class)
public class SetterDTO {
    
    private int someInt;

    public int getSomeInt() {
        return someInt;
    }
    
    @Source("related.myInt")
    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }
    
}
