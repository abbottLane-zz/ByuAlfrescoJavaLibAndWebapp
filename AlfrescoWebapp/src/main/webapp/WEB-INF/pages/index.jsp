<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Alfresco Java Plugin Demo</title>
    <link href="<c:url value="/resources/css/theme.css" />" rel="stylesheet"/>
    <link rel="shortcut icon" href="https://www.byu.edu/templates/2.1.5/images/favicon.ico">
    <link href="http://getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="http://getbootstrap.com/dist/css/bootstrap-responsive.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1>Alfresco Webapp Login</h1>
        <!-- Method == method, action == action, commandName == the method in the controller we want to call -->
        <form:form method="post" action="login" commandName="login" role="form">
        <div class="form-group">
            <form:label path="user">Username:</form:label>
            <form:input path="user" class="form-control" placeholder="User"/>
        </div>
        <div class="form-group">
            <form:label path="password">Password:</form:label>
            <form:input path="password" type= "password" class="form-control" placeholder="Password"/>
        </div>
        <form:select path="environment" commandName="environment">
            <form:option value="localhost" label="My Alfresco Deployment"/>
            <form:option value="dev" label="BYU Dev"/>
            <form:option value="stg" label="BYU Stg"/>
            <form:option value="prd" label="BYU Prod"/>
        </form:select>
        <br>
        <br>
        <button type="submit" class="btn btn-default">Login</button>
        </form:form>

        <div class="errorMessage">
            <c:if test="${loginError == true}">
                <p>Invalid user credentials for environment: ${errorEnvironment}</p>
            </c:if>
        </div>
    </div>
</body>
</html>