<#-- Сторінка перегляду заявок абітурієнта -->
<#import "layout.ftl" as layout>
<@layout.page title="Заявки абітурієнта">
    <h1>Перегляд заявок</h1>

    <form method="get" action="/applications" class="search-form">
        <div class="form-group">
            <label for="applicantId">ID абітурієнта</label>
            <input type="text" id="applicantId" name="applicantId" value="${(applicantId)!""}" required>
        </div>
        <button type="submit" class="btn-primary">Знайти</button>
    </form>

    <#if applications??>
        <#if applications?size == 0>
            <div class="alert-info">Заявок не знайдено.</div>
        <#else>
            <table class="applications-table">
                <thead>
                <tr>
                    <th>Факультет</th>
                    <th>Бали</th>
                    <th>Статус</th>
                </tr>
                </thead>
                <tbody>
                <#list applications as app>
                    <tr class="${app.status()?lower_case}">
                        <td>${app.facultyName()}</td>
                        <td>${app.totalScore()}</td>
                        <td>${app.status()}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </#if>
    </#if>

    <style>
        h1 { color: #1f2937; margin-bottom: 20px; }
        .search-form { max-width: 400px; margin-bottom: 30px; }
        .form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
        label { margin-bottom: 5px; font-weight: 500; }
        input[type="text"] { padding: 8px 10px; border-radius: 5px; border: 1px solid #d1d5db; }
        .btn-primary { background: #2563eb; color: #fff; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; transition: background 0.2s; }
        .btn-primary:hover { background: #1d4ed8; }
        .alert-info { background: #dbeafe; color: #1e40af; padding: 10px 15px; border-radius: 5px; margin-top: 15px; }

        .applications-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        .applications-table th, .applications-table td { border: 1px solid #e5e7eb; padding: 10px 12px; text-align: left; }
        .applications-table th { background: #f3f4f6; font-weight: 600; }

        /* Статуси */
        .pending { background-color: #fef3c7; }  /* Очікує */
        .accepted { background-color: #d1fae5; } /* Зараховано */
        .rejected { background-color: #fee2e2; } /* Відхилено */
    </style>
</@layout.page>
