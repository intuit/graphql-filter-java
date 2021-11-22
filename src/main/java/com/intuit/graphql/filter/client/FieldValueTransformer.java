/*
  Copyright 2021 Intuit Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.intuit.graphql.filter.client;

/**
 * Transformer interface for customizing
 * filter field and value.
 *
 * @author sjaiswal on 11/18/21
 */
public interface FieldValueTransformer {

    /**
     * Returns a new field name for the given field name.
     * @param fieldName
     * @return
     */
    public String transformField(String fieldName);

    /**
     * Returns an instance of FieldValuePair.
     * @param fieldName
     * @param value
     * @return
     */
    public FieldValuePair<? extends Object> transformValue(String fieldName, Object value);
}

