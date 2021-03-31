/*
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package com.amazon.opendistroforelasticsearch.commons.notifications.model

import com.amazon.opendistroforelasticsearch.notifications.createObjectFromJsonString
import com.amazon.opendistroforelasticsearch.notifications.getJsonString
import com.amazon.opendistroforelasticsearch.notifications.util.recreateObject
import org.elasticsearch.test.ESTestCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FilterConfigTests : ESTestCase() {

    @Test
    fun `Config serialize and deserialize with default isEnabled flag should be equal`() {
        val sampleConfig = FeatureConfig(
            "configId",
            "name",
            "description",
            NotificationConfig.ConfigType.Slack
        )
        val recreatedObject = recreateObject(sampleConfig) { FeatureConfig(it) }
        assertEquals(sampleConfig, recreatedObject)
    }

    @Test
    fun `Config serialize and deserialize with isEnabled=false should be equal`() {
        val sampleConfig = FeatureConfig(
            "configId",
            "name",
            "description",
            NotificationConfig.ConfigType.Chime,
            false
        )
        val recreatedObject = recreateObject(sampleConfig) { FeatureConfig(it) }
        assertEquals(sampleConfig, recreatedObject)
    }

    @Test
    fun `Config serialize and deserialize using json object with default isEnabled flag should be equal`() {
        val sampleConfig = FeatureConfig(
            "configId",
            "name",
            "description",
            NotificationConfig.ConfigType.Webhook
        )
        val jsonString = getJsonString(sampleConfig)
        val recreatedObject = createObjectFromJsonString(jsonString) { FeatureConfig.parse(it) }
        assertEquals(sampleConfig, recreatedObject)
    }

    @Test
    fun `Config serialize and deserialize using json object with isEnabled=false should be equal`() {
        val sampleConfig = FeatureConfig(
            "configId",
            "name",
            "description",
            NotificationConfig.ConfigType.EmailGroup,
            false
        )
        val jsonString = getJsonString(sampleConfig)
        val recreatedObject = createObjectFromJsonString(jsonString) { FeatureConfig.parse(it) }
        assertEquals(sampleConfig, recreatedObject)
    }

    @Test
    fun `Config should safely ignore extra field in json object`() {
        val sampleConfig = FeatureConfig(
            "configId",
            "name",
            "description",
            NotificationConfig.ConfigType.Email
        )
        val jsonString = """
        {
            "configId":"configId",
            "name":"name",
            "description":"description",
            "configType":"Email",
            "isEnabled":true,
            "extra_field_1":["extra", "value"],
            "extra_field_2":{"extra":"value"},
            "extra_field_3":"extra value 3"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { FeatureConfig.parse(it) }
        assertEquals(sampleConfig, recreatedObject)
    }

    @Test
    fun `Config should safely ignore unknown config type in json object`() {
        val sampleConfig = FeatureConfig(
            "configId",
            "name",
            "description",
            NotificationConfig.ConfigType.None
        )
        val jsonString = """
        {
            "configId":"configId",
            "name":"name",
            "description":"description",
            "configType":"NewConfig"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { FeatureConfig.parse(it) }
        assertEquals(sampleConfig, recreatedObject)
    }

    @Test
    fun `Config throw exception if configId is empty`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            FeatureConfig(
                "",
                "name",
                "description",
                NotificationConfig.ConfigType.EmailGroup
            )
        }
    }

    @Test
    fun `Config throw exception if name is empty`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            FeatureConfig(
                "configId",
                "",
                "description",
                NotificationConfig.ConfigType.EmailGroup
            )
        }
    }
}
