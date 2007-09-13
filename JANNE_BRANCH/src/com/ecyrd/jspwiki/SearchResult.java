/* 
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.ecyrd.jspwiki;

// FIXME: Move to the search-package
public interface SearchResult
{
    /**
     *  Return the page.
     */
    public WikiPage getPage();

    /**
     *  Returns the score.
     */

    public int getScore();
    
    
    /**
     * Collection of XHTML fragments representing some contexts in which
     * the match was made (a.k.a., "snippets").
     *
     * @return the search results
     * @since 2.4
     */
    public String[] getContexts();
}