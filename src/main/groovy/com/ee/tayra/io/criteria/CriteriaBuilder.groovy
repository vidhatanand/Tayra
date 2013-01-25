package com.ee.tayra.io.criteria;

import java.util.ArrayList;
import java.util.List;

import com.ee.tayra.io.criteria.Criterion;
import com.ee.tayra.io.criteria.DbCriteria;
import com.ee.tayra.io.criteria.MultiCriteria;
import com.ee.tayra.io.criteria.TimestampCriteria;

public class CriteriaBuilder {

  private List<Criterion> criteria = new ArrayList<Criterion>();

  public void usingDatabase(String db) {
    criteria.add(new DbCriteria(db));
  }

  public void usingUntil(String timestamp) {
    criteria.add(new TimestampCriteria(timestamp));
  }

  public Criterion build(Closure closure = {}) {
    def clonedClosure = closure.clone()
    clonedClosure.resolveStrategy = Closure.DELEGATE_FIRST
    clonedClosure.delegate = this
    clonedClosure()
    if(criteria.isEmpty()) {
      return Criterion.ALL
    }
    return new MultiCriteria(criteria);
  }
}