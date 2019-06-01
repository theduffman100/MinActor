package com.l2o.minactor;

public interface SimpleCloseable extends AutoCloseable {
  @Override
  void close();
}
