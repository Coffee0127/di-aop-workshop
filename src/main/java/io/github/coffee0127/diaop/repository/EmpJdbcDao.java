package io.github.coffee0127.diaop.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmpJdbcDao implements EmpDao {

  private static final String INSERT_STMT =
      "INSERT INTO emp2 (ename, job, hiredate, sal, comm, deptno)"
          + " VALUES (:ename, :job, :hiredate, :sal, :comm, :deptno)";
  private static final String GET_ALL_STMT =
      "SELECT empno,ename,job,to_char(hiredate,'yyyy-mm-dd') hiredate,sal,comm,deptno FROM emp2 ORDER BY empno";
  private static final String GET_ONE_STMT =
      "SELECT empno,ename,job,to_char(hiredate,'yyyy-mm-dd') hiredate,sal,comm,deptno FROM emp2 WHERE empno = ?";
  private static final String UPDATE =
      "UPDATE emp2"
          + " SET ename=:ename, job=:job, hiredate=:hiredate, sal=:sal, comm=:comm, deptno=:deptno"
          + " WHERE empno = :empno";
  private static final String DELETE = "DELETE FROM emp2 WHERE empno = :empno";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNameTemplate;

  public EmpJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNameTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNameTemplate = jdbcNameTemplate;
  }

  @Override
  public Emp insert(Emp emp) {
    var paramSource = new BeanPropertySqlParameterSource(emp);
    var keyHolder = new GeneratedKeyHolder();
    jdbcNameTemplate.update(INSERT_STMT, paramSource, keyHolder);
    var key = keyHolder.getKey();
    if (key == null) {
      throw new IllegalStateException("Cannot get the generated key");
    }
    var empno = key.intValue();
    emp.setEmpno(empno);
    return emp;
  }

  @Override
  public List<Emp> getAll() {
    return jdbcTemplate.query(GET_ALL_STMT, new BeanPropertyRowMapper<>(Emp.class));
  }

  @Override
  public Emp findByPrimaryKey(Integer empno) {
    return jdbcTemplate.queryForObject(GET_ONE_STMT, new BeanPropertyRowMapper<>(Emp.class), empno);
  }

  @Override
  public Emp update(Emp emp) {
    var paramSource = new BeanPropertySqlParameterSource(emp);
    jdbcNameTemplate.update(UPDATE, paramSource);
    return emp;
  }

  @Override
  public int delete(Integer empno) {
    var paramSource = new MapSqlParameterSource("empno", empno);
    return jdbcNameTemplate.update(DELETE, paramSource);
  }
}
