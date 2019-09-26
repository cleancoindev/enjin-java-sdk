package com.enjin.enjincoin.sdk.graphql.api;

import com.enjin.enjincoin.sdk.graphql.GraphQLTemplate;
import com.google.common.truth.Truth;
import org.junit.Test;

import java.util.List;

public class TemplatesTest {

    // Tests will fail if new templates are added and this isn't updated
    private static final int TEMPLATE_COUNT = 29;

    @Test
    public void getTemplates_returnsCorrectNumberOfTemplates() {
        List<GraphQLTemplate> templates = Templates.getTemplates();
        Truth.assertThat(templates).hasSize(TEMPLATE_COUNT);
    }

}