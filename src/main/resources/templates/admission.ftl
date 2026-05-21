<#-- Сторінка зарахування абітурієнтів (лише для адміністратора) -->
<#import "layout.ftl" as layout>
<@layout.page title="Зарахування">
    <h1>Провести зарахування</h1>

    <#if error??>
        <div class="alert alert-error">${error}</div>
    </#if>

    <form method="post" action="/admission" class="admission-form">
        <#if _csrf??>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        </#if>

        <div class="form-group">
            <label for="faculty">Факультет</label>
            <select name="facultyName" id="faculty" required>
                <option value="">-- Оберіть факультет --</option>
                <#if faculties??>
                    <#list faculties as faculty>
                        <option value="${faculty.name}">${faculty.name}</option>
                    </#list>
                </#if>
            </select>
        </div>

        <button type="submit" class="btn-primary">Провести зарахування</button>
    </form>

    <#if result??>
        <div class="result-section">
            <h2>Результати зарахування: ${result.facultyName()}</h2>

            <div class="result-block admitted">
                <h3>Зараховано (${result.admitted()?size})</h3>
                <#if result.admitted()?size gt 0>
                    <table class="result-table">
                        <thead>
                        <tr><th>Абітурієнт</th><th>Бали</th></tr>
                        </thead>
                        <tbody>
                        <#list result.admitted() as app>
                            <tr><td>${app.applicantName()}</td><td>${app.totalScore()}</td></tr>
                        </#list>
                        </tbody>
                    </table>
                <#else>
                    <p>Немає зарахованих.</p>
                </#if>
            </div>

            <div class="result-block rejected">
                <h3>Відхилено (${result.rejected()?size})</h3>
                <#if result.rejected()?size gt 0>
                    <table class="result-table">
                        <thead>
                        <tr><th>Абітурієнт</th><th>Бали</th></tr>
                        </thead>
                        <tbody>
                        <#list result.rejected() as app>
                            <tr><td>${app.applicantName()}</td><td>${app.totalScore()}</td></tr>
                        </#list>
                        </tbody>
                    </table>
                <#else>
                    <p>Немає відхилених.</p>
                </#if>
            </div>
        </div>
    </#if>

    <style>
        h1 { color: #1f2937; margin-bottom: 20px; }
        .alert-error { background: #fee2e2; color: #b91c1c; padding: 10px 15px; border-radius: 5px; margin-bottom: 20px; }
        .admission-form { max-width: 400px; margin-bottom: 30px; }
        .form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
        label { margin-bottom: 5px; font-weight: 500; }
        select { padding: 8px 10px; border-radius: 5px; border: 1px solid #d1d5db; }
        .btn-primary { background: #2563eb; color: #fff; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; transition: background 0.2s; }
        .btn-primary:hover { background: #1d4ed8; }
        .result-section { margin-top: 30px; }
        .result-block { margin-bottom: 25px; }
        .result-block h3 { margin-bottom: 10px; }
        .result-table { width: 100%; border-collapse: collapse; }
        .result-table th, .result-table td { border: 1px solid #e5e7eb; padding: 8px 12px; text-align: left; }
        .result-table th { background: #f3f4f6; }
        .admitted table tr { background-color: #d1fae5; }
        .rejected table tr { background-color: #fee2e2; }
    </style>
</@layout.page>
