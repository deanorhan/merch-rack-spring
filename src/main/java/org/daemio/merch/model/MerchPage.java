package org.daemio.merch.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.daemio.merch.domain.Merch;

@Getter
@Setter
@NoArgsConstructor
public class MerchPage {
    
    private List<MerchModel> merch = new ArrayList<>();

    private int page;
    private int size;
    private int totalPages;

    // @JsonIgnore
    public void addMerch(MerchModel merchModel) {
        merch.add(merchModel);
    }
}
