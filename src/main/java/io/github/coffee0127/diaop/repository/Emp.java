package io.github.coffee0127.diaop.repository;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

@Accessors(chain = true)
@Data
public class Emp {
  private Integer empno;
  private String ename;
  private String job;
  private Date hiredate;
  private Double sal;
  private Double comm;
  private Integer deptno;
}
