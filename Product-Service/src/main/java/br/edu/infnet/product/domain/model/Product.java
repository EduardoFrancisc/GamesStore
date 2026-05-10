package br.edu.infnet.product.domain.model;

import br.edu.infnet.product.domain.enums.Platform;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "products")
public class Product {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword) //Keyword para buscas exatas no Elasticsearch
    private Platform platform;

    @Field(type = FieldType.Integer)
    private Integer stockQuantity;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime releaseDate;
}
