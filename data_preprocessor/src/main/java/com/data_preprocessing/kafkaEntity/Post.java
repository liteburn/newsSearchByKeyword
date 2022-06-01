package com.data_preprocessing.kafkaEntity;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Post implements Serializable {
    String href;
    String pathToFile;
    String title;
}
