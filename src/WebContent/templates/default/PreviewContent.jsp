<%@ taglib uri="http://jakarta.apache.org/jspwiki.tld" prefix="wiki" %>
<%@ page import="org.apache.wiki.ui.EditorManager" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>

<%-- Inserts page content for preview. --%>
<wiki:TabbedSection>
<wiki:Tab id="previewcontent" titleKey="preview.tab">

  <div class="information">
    <fmt:message key="preview.info" />
    <wiki:Editor/>
  </div>

  <div class="previewcontent">
    <wiki:Translate><%=EditorManager.getEditedText(pageContext)%></wiki:Translate>
  </div>

  <div class="information">
    <fmt:message key="preview.info" />
  </div>

</wiki:Tab>
</wiki:TabbedSection>