package com.example.app.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	    throws IOException, ServletException {

	  HttpServletRequest req = (HttpServletRequest) request;
	  HttpServletResponse res = (HttpServletResponse) response;

	  if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
	    chain.doFilter(request, response);
	    return;
	  }

	  String uri = req.getRequestURI();
	  String ctx = req.getContextPath();

	  // 以下は今の処理のまま

    // 戻る対策（キャッシュ無効）
    res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    res.setHeader("Pragma", "no-cache");
    res.setHeader("Expires", "0");

    // ✅ このフィルタが見る対象を「ログイン後エリア」に限定
    boolean adminArea  = uri.startsWith(ctx + "/admins/");
    boolean memberArea = uri.startsWith(ctx + "/members/club/"); // ←ログイン後エリアだけ

    if (!adminArea && !memberArea) {
      chain.doFilter(request, response);
      return;
    }

    // ✅ ログイン画面系は素通り（adminAreaにfilterを当てるなら保険として残す）
    if (uri.equals(ctx + "/admins/adminslogin")
        || uri.equals(ctx + "/members/memberslogin")
        || uri.equals(ctx + "/members/logout")
        || uri.equals(ctx + "/logout")
        || uri.equals(ctx + "/loginhome")) {
      chain.doFilter(request, response);
      return;
    }

    HttpSession session = req.getSession(false);

    if (adminArea) {
      Object loginId = (session == null) ? null : session.getAttribute("loginId");
      if (loginId == null) {
        res.sendRedirect(ctx + "/loginhome");
        return;
      }
    }

    if (memberArea) {
      Object memberId = (session == null) ? null : session.getAttribute("memberId");
      if (memberId == null) {
        res.sendRedirect(ctx + "/members/memberslogin");
        return;
      }
    }

    chain.doFilter(request, response);
  }
}