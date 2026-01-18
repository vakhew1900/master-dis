package org.master.diploma.git.graph.subgraphmethod.llm;

import org.aeonbits.owner.Config;

public interface LlmConfig extends Config {

    @Key("model")
    String model();

    @Key("api-key")
    String apiKey();

    @Key("promt")
    String promt();
    @Key("baseUrl")
    String baseUrl();
}
