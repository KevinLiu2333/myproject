<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- Horizontal Form -->
<div class="box box-info">
  <div class="box-header with-border">
  </div>
  <!-- /.box-header -->
  <!-- form start -->
  <form id="userForm" name="userForm" class="form-horizontal" role="form" method="post"
        data-fv-message="This value is not valid"
        data-fv-feedbackicons-valid="glyphicon glyphicon-ok"
        data-fv-feedbackicons-invalid="glyphicon glyphicon-remove"
        data-fv-feedbackicons-validating="glyphicon glyphicon-refresh">
    <div class="box-body">
      <div class="form-group">
        <label class="control-label col-sm-2 no-padding-right" for="accountName">登录名</label>
        <div class="col-sm-4">
            <input class="form-control" name="accountName" id="accountName" type="text" placeholder="将做为用户登录系统的用户名..."
                   data-fv-notempty="true"
                   data-fv-message="登录名不能为空"/>
        </div>
        <label class="control-label col-sm-2 no-padding-right" for="userName">真实姓名</label>
        <div class="col-sm-4">
            <input class="form-control" name="userName" id="userName" type="text" placeholder="真实姓名..."
                   data-fv-notempty="true"
                   data-fv-message="真实姓名不能为空"/>
        </div>
      </div>
        <div class="form-group">
          <label class="control-label col-sm-2 no-padding-right" for="password">密码</label>
          <div class="col-sm-4">
              <input class="form-control" name="password" id="password" type="password" placeholder="密码..."
                     data-fv-notempty="true"
                     data-fv-message="密码不能为空"/>
          </div>
          <label class="control-label col-sm-2 no-padding-right" for="repassword">确认密码</label>
          <div class="col-sm-4">
            <div class="clearfix">
              <input class="form-control" name="repassword" id="repassword" type="password"
                     placeholder="确认密码..."
                     data-fv-notempty="true"
                     data-fv-message="确认密码不能为空"/>
            </div>
          </div>
        </div>
      <div class="form-group">
        <label class="control-label col-sm-2 no-padding-right" for="userName">所属角色</label>
        <div class="col-sm-10">
            <select class="form-control" name="role.id" id="roleId" style="width: 100%">
              <option value="">请选择角色...</option>
                  <c:forEach var="role" items="${roleList }">
                    <option value="${role.id }">${role.name }</option>
                  </c:forEach>
            </select>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2 no-padding-right" for="description">用户描述</label>
        <div class="col-sm-10">
          <textarea class="form-control" name="description" id="description" rows="3"></textarea>
        </div>
      </div>
    </div>
    <!-- /.box-body -->
    <div class="box-footer">
      <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
          <button id="btnAdd" type="button" onclick="commit('userForm','/user/add');" class="btn btn-success btn-sm">
            <i class="fa fa-user-plus"></i>&nbsp;添加
          </button>
          <button id="btn" type="button" onclick="closeModel()" class="btn btn-info btn-sm">
            <i class="fa fa-close"></i>&nbsp;取消
          </button>
          </div>
        </div>
    </div>
    <!-- /.box-footer -->
  </form>
</div>

<script>
  $(function(){
    $("#userForm").formValidation();
  });
</script>