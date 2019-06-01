package com.l2o.minactor.call;

public interface CallWithParam<PARAM, RESULT> extends Call<RESULT> {
  PARAM getParam();
}
