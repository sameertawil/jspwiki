<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>

<%-- Inserts page content for preview. --%>

   <DIV class="previewnote">
      <B>This is a PREVIEW!  Hit "back" on your browser to go back to the editor,
      or hit "Save" if you're happy with what you see.</B>
   </DIV>

   <P><HR></P>

   <DIV class="previewcontent">
      <wiki:Translate><%=pageContext.getAttribute("usertext",PageContext.REQUEST_SCOPE)%></wiki:Translate>
   </DIV>

   <BR clear="all" />

   <P><HR></P>

   <DIV class="previewnote">
      <B>This is a PREVIEW!  Hit "back" on your browser to go back to the editor,
      or hit "Save" if you're happy with what you see.</B>
   </DIV>

   <p><hr /></p>

   <form action="<wiki:EditLink format="url" />" method="POST" 
         ACCEPT-CHARSET="<wiki:ContentEncoding />">
   <p>

   <%-- These are required parts of this form.  If you do not include these,
        horrible things will happen.  Do not modify them either. --%>

   <input type="hidden" name="page"     value="<wiki:PageName/>">
   <input type="hidden" name="action"   value="save">
   <input type="hidden" name="edittime" value="<%=pageContext.getAttribute("lastchange", PageContext.REQUEST_SCOPE )%>">
   <input type="hidden" name="text"     value="<%=pageContext.getAttribute("usertext", PageContext.REQUEST_SCOPE) %>">

   <div id="previewsavebutton" align="center">
      <input type="submit" name="ok" value="Save" />
   </div>

   </p>
   </form>