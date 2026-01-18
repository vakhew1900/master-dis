package org.master.diploma.git.graph;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.master.diploma.git.json.JsonGraph;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public  class JsonPairGraph {

    public static final String FIRST = "first";
    public static final String SECOND = "second";

    @SerializedName(FIRST)
    JsonGraph first;
    @SerializedName(SECOND)
    JsonGraph second;
}