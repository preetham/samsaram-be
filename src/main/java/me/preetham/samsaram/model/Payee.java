package me.preetham.samsaram.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "search-payee")
@Getter
public class Payee {

  @Id
  private String id;
  @Field(type = FieldType.Text)
  @Setter
  private String name;
  @Field(type = FieldType.Text)
  private String logo;
  @Field(type = FieldType.Text)
  private String merchantIdentifier;
  @Field(type = FieldType.Integer)
  private Integer category;

  public Payee() {
    this.id = "";
    this.name = "Unknown";
    this.logo = "";
    this.merchantIdentifier = "";
    this.category = 27;
  }
}
