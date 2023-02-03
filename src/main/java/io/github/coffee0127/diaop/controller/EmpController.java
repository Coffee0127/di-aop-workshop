package io.github.coffee0127.diaop.controller;

import io.github.coffee0127.diaop.repository.Emp;
import io.github.coffee0127.diaop.repository.EmpDao;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmpController {

  private final EmpDao empDao;

  public EmpController(EmpDao empDao) {
    this.empDao = empDao;
  }

  @PostMapping
  public Emp create(@RequestBody Emp emp) {
    emp.setEmpno(null);
    return empDao.insert(emp);
  }

  @PutMapping
  public Emp update(@RequestBody Emp emp) {
    return empDao.update(emp);
  }

  @GetMapping
  public List<Emp> query() {
    return empDao.getAll();
  }

  @GetMapping("/{empno}")
  public Emp query(@PathVariable Integer empno) {
    return empDao.findByPrimaryKey(empno);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{empno}")
  public void delete(@PathVariable Integer empno) {
    empDao.delete(empno);
  }
}
