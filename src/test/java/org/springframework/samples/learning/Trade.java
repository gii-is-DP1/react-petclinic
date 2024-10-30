package org.springframework.samples.learning;

import lombok.Data;

@Data
public class Trade {
    private long id = 0;
    private String name = "";
    private String desc = "";

    public Trade(long id) {
        this.id = id;
    }

	public Trade(String name, String desc) {
        this.name = name;
        this.desc = desc;
	}


}
