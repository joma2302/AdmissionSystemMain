<#import "layout.ftl" as layout>
<@layout.page title="Редагування профілю">
    <h1>Редагування профілю</h1>

    <#if success??>
        <div class="alert alert-success">Дані успішно оновлено!</div>
    </#if>
    <#if error??>
        <div class="alert alert-error">${error}</div>
    </#if>

    <#if hasApplications>
        <div class="alert alert-warning">
            Ви вже подали заявку, тому редагування персональних даних заборонено.
        </div>
        <a href="/applications?applicantId=${applicant.id}" class="btn-secondary">Назад до заявок</a>
    <#else>
        <form method="post" action="/profile/edit" class="profile-form">
            <#if _csrf??>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </#if>
            <input type="hidden" name="applicantId" value="${applicant.id}">

            <div class="form-group">
                <label>ID абітурієнта</label>
                <input type="text" value="${applicant.id}" disabled>
            </div>

            <div class="form-group">
                <label for="firstName">Ім'я</label>
                <input type="text" name="firstName" id="firstName" value="${applicant.firstName}" required>
            </div>

            <div class="form-group">
                <label for="lastName">Прізвище</label>
                <input type="text" name="lastName" id="lastName" value="${applicant.lastName}" required>
            </div>

            <button type="submit" class="btn-primary">Зберегти</button>
            <a href="/" class="btn-secondary">Скасувати</a>
        </form>
    </#if>

    <style>
        .profile-form { max-width: 500px; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; font-weight: 500; }
        input[type="text"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
        .alert-success { background: #d1fae5; color: #065f46; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .alert-error { background: #fee2e2; color: #b91c1c; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .alert-warning { background: #fef3c7; color: #92400e; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .btn-primary { background: #3b82f6; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-secondary { background: #6b7280; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; }
    </style>
</@layout.page>
