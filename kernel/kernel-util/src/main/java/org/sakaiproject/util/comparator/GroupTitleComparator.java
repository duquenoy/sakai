/***********************************************************************************
* Copyright (c) 2020 Apereo Foundation

* Licensed under the Educational Community License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
             http://opensource.org/licenses/ecl2
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 **********************************************************************************/

package org.sakaiproject.util.comparator;

import java.util.Comparator;

import org.sakaiproject.site.api.Group;

public class GroupTitleComparator implements Comparator<Group> {
    public int compare(Group lhs, Group rhs) {
        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
    }
}
