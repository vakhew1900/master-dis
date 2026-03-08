package org.master.diploma.git.graph.dto.samples;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing label differences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffDto {
    public static final String STATE_EXTRACT = "EXTRACT";
    public static final String STATE_MISSED = "MISSED";
    public static final String STATE_CORRECT = "CORRECT";

    @SerializedName("value")
    private String value;
    @SerializedName("state")
    private String state;
}
