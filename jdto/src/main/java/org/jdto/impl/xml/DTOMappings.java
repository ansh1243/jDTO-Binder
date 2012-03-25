/*
 *    Copyright 2011 Juan Alberto López Cavallotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jdto.impl.xml;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The DTO Mappgins root element.
 * @author Juan Alberto Lopez Cavallotti
 */
@XmlRootElement(name = "dto-mapping")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DTOMappings implements Serializable {

    private static final long serialVersionUID = 1L;

    public DTOMappings() {
    }
    private List<DTOElement> elements;

    @XmlElement(name = "dto")
    public List<DTOElement> getElements() {
        return elements;
    }

    public void setElements(List<DTOElement> elements) {
        this.elements = elements;
    }
}
