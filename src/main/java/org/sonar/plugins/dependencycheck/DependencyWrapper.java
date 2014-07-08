/*
 * Sonar Dependency Check Plugin
 * Copyright (C) 2013 Porsche Informatik
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="allowedProjectDependencies")
@XmlAccessorType(XmlAccessType.FIELD)
public class DependencyWrapper {

	@XmlElementWrapper (name="projectDependencies")
	@XmlElement (name="dependency")
	private ArrayList<ProjectDependency> list= new ArrayList<ProjectDependency>();

	//getter and setter
	public ArrayList<ProjectDependency> getList() {
		return list;
	}
	
	
	public void setList(ArrayList<ProjectDependency> list) {
		this.list = list;
	}
	
	@Override
	public String toString(){
		String result="";
		int i=1;
		if(!list.isEmpty()){
			for(ProjectDependency proDp: list){
				result+=i++ +"." +"Element: " + proDp.toString()+ " ";
			}
		return result;
		}
		else{
			return ("List is empty - no elements included in the allowed-projectDependencies-list!");
		}
	}
	
	
}
