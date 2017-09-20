<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<header class="application-actions-header">
  <div class="application-actions-container">
    <div class="application-action icon-view"></div>
    <div class="application-action icon-edit" id="action-application-edit"></div>
    <c:choose>
      <c:when test="${infoApplicantEditable eq true}">
        <div class="application-action icon-unlocked" id="action-application-toggle-lock"></div>
      </c:when>
      <c:otherwise>
        <div class="application-action icon-locked" id="action-application-toggle-lock"></div>
      </c:otherwise>
    </c:choose>
    <div class="application-action icon-mail"></div>
    <div class="application-action icon-logs" id="action-application-log"></div>
  </div>
</header>