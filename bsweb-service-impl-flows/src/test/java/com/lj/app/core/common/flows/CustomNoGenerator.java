package com.lj.app.core.common.flows;

import com.lj.app.core.common.flows.model.ProcessModel;

/**
 * 编号生成器接口 流程实例的编号字段使用该接口实现类来产生对应的编号
 */
public class CustomNoGenerator implements INoGenerator {

  /**
   * 生成器方法
   * 
   * @param model 流程模型
   * @return String 编号
   */
  public String generate(ProcessModel model) {
    return java.util.UUID.randomUUID().toString().replace("-", "");
  }
}
