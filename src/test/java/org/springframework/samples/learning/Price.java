package org.springframework.samples.learning;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    private int price = 0;

	public Price add(Price sumPrice) {
        return new Price(this.getPrice() + sumPrice.getPrice());
	}

	public int getAmount() {
		return price;
	}
}
