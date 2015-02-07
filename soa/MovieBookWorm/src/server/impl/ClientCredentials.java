/*
 * Copyright (c) 2015 Nemanja Stolic
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package server.impl;


/**
 * API key found in the <a href="https://code.google.com/apis/console?api=books">Google apis
 * console</a>.
 * 
 * <p>
 * Once at the Google apis console, click on "Add project...". If you've already set up a project,
 * you may use that one instead, or create a new one by clicking on the arrow next to the project
 * name and click on "Create..." under "Other projects". Finally, click on "API Access". Look for
 * the section at the bottom called "Simple API Access".
 * </p>
 * 
 * @author Nemanja Stolic
 */
public class ClientCredentials {

  /** Value of the "API key" shown under "Simple API Access". */
	//"Enter API Key from https://code.google.com/apis/console/?api=books into API_KEY in "
  static final String API_KEY =
      "AIzaSyDhJtv2ERtoek8G0G23ArkPnc6qzE2tBjU"
      + ClientCredentials.class;

  static void errorIfNotSpecified() {
    if (API_KEY.startsWith("Enter ")) {
      System.err.println(API_KEY);
      System.exit(1);
    }
  }
}