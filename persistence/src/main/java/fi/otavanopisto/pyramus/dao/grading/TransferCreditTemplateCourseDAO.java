package fi.otavanopisto.pyramus.dao.grading;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;

import fi.otavanopisto.pyramus.dao.PyramusEntityDAO;
import fi.otavanopisto.pyramus.domainmodel.base.CourseOptionality;
import fi.otavanopisto.pyramus.domainmodel.base.Curriculum;
import fi.otavanopisto.pyramus.domainmodel.base.EducationalLength;
import fi.otavanopisto.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.otavanopisto.pyramus.domainmodel.base.Subject;
import fi.otavanopisto.pyramus.domainmodel.grading.TransferCreditTemplate;
import fi.otavanopisto.pyramus.domainmodel.grading.TransferCreditTemplateCourse;
import fi.otavanopisto.pyramus.domainmodel.grading.TransferCreditTemplateCourse_;
import fi.otavanopisto.pyramus.persistence.search.SearchResult;

@Stateless
public class TransferCreditTemplateCourseDAO extends PyramusEntityDAO<TransferCreditTemplateCourse> {

  @SuppressWarnings("unchecked")
  public SearchResult<TransferCreditTemplateCourse> searchTransferCreditTemplateCoursesBasic(int resultsPerPage, int page, String text) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "courseName", text, false);
      queryBuilder.append(")");
    }

    EntityManager entityManager = getEntityManager();
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

    try {
      String queryString = queryBuilder.toString();
      Query luceneQuery;
      QueryParser parser = new QueryParser("", new StandardAnalyzer());
      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      } else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, TransferCreditTemplateCourse.class)
        .setFirstResult(firstResult)
        .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<>(page, pages, hits, firstResult, lastResult, query.getResultList());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }
  
  /* TransferCreditTemplateCourse */
  
  public TransferCreditTemplateCourse create(TransferCreditTemplate transferCreditTemplate, String courseName, Integer courseNumber, CourseOptionality optionality, Double courseLength, EducationalTimeUnit courseLengthUnit, Subject subject, Curriculum curriculum) {
    EntityManager entityManager = getEntityManager();
    
    EducationalLength courseEductionalLength = new EducationalLength();
    courseEductionalLength.setUnits(courseLength);
    courseEductionalLength.setUnit(courseLengthUnit);
    entityManager.persist(courseEductionalLength);
    
    TransferCreditTemplateCourse transferCreditTemplateCourse = new TransferCreditTemplateCourse();
    transferCreditTemplateCourse.setCourseLength(courseEductionalLength);
    transferCreditTemplateCourse.setCourseName(courseName);
    transferCreditTemplateCourse.setCourseNumber(courseNumber);
    transferCreditTemplateCourse.setOptionality(optionality);
    transferCreditTemplateCourse.setSubject(subject);
    transferCreditTemplateCourse.setCurriculum(curriculum);

    entityManager.persist(transferCreditTemplateCourse);
    
    transferCreditTemplate.addCourse(transferCreditTemplateCourse);

    entityManager.persist(transferCreditTemplate);
  
    return transferCreditTemplateCourse;
  }
  
  public TransferCreditTemplateCourse update(TransferCreditTemplateCourse transferCreditTemplateCourse, String courseName, Integer courseNumber, CourseOptionality optionality, Double courseLength, EducationalTimeUnit courseLengthUnit, Subject subject, Curriculum curriculum) {
    EntityManager entityManager = getEntityManager();
    
    EducationalLength educationalLength = transferCreditTemplateCourse.getCourseLength();
    educationalLength.setUnits(courseLength);
    educationalLength.setUnit(courseLengthUnit);
    entityManager.persist(educationalLength);
    
    transferCreditTemplateCourse.setCourseName(courseName);
    transferCreditTemplateCourse.setCourseNumber(courseNumber);
    transferCreditTemplateCourse.setOptionality(optionality);
    transferCreditTemplateCourse.setSubject(subject);
    transferCreditTemplateCourse.setCurriculum(curriculum);

    entityManager.persist(transferCreditTemplateCourse);
    
    return transferCreditTemplateCourse;
  }
  
  public List<TransferCreditTemplateCourse> listByTemplate(TransferCreditTemplate template) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<TransferCreditTemplateCourse> criteria = criteriaBuilder.createQuery(TransferCreditTemplateCourse.class);
    Root<TransferCreditTemplateCourse> root = criteria.from(TransferCreditTemplateCourse.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.equal(root.get(TransferCreditTemplateCourse_.transferCreditTemplate), template)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  @Override
  public void delete(TransferCreditTemplateCourse transferCreditTemplateCourse) {
    super.delete(transferCreditTemplateCourse);
  }

}
