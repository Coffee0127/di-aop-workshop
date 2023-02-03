package io.github.coffee0127.diaop.repository;

import java.util.List;

public interface EmpDao {
  Emp insert(Emp emp);

  List<Emp> getAll();

  Emp findByPrimaryKey(Integer empno);

  Emp update(Emp emp);

  int delete(Integer empno);
}
