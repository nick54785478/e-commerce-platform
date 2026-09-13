package com.example.demo.infra.mapper;

public interface BaseDataTransform<Source, Target> {
	Target transformACL(Source source);
}
