package com.dtstack.batch.domain;

import lombok.Data;

@Data
public class BatchCatalogue extends Catalogue {

    BatchCatalogue parentCatalogue;

}
