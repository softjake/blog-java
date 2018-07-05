package com.softjake.blog.reactive.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Member {
  @NonNull private int id;
  @NonNull private String firstName;
  @NonNull private String lastName;
  @NonNull private String email;
  @NonNull private String city;
}
