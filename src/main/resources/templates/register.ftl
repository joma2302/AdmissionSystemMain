<#-- Сторінка реєстрації абітурієнта з оцінками -->
<#import "layout.ftl" as layout>
<@layout.page title="Реєстрація абітурієнта">

    <style>
        .auth-wrapper{
            max-width: 720px;
            margin: 40px auto;
            padding: 30px 28px;
            background: #fff;
            border-radius: 14px;
            box-shadow: 0 12px 32px rgba(0,0,0,0.08);
        }

        h1{
            text-align:center;
            margin-bottom:20px;
        }

        .section-title{
            margin-top:26px;
            margin-bottom:14px;
            font-size:18px;
            font-weight:600;
            color:#333;
            border-bottom:1px solid #eee;
            padding-bottom:6px;
        }

        .form-grid{
            display:grid;
            grid-template-columns: 1fr 1fr;
            gap:16px;
        }

        .form-group{
            display:flex;
            flex-direction:column;
            margin-bottom:14px;
        }

        .form-group label{
            font-size:14px;
            margin-bottom:6px;
            color:#444;
        }

        input{
            padding:10px 12px;
            border-radius:8px;
            border:1px solid #ccc;
            font-size:15px;
            transition:border .2s, box-shadow .2s;
        }

        input:focus{
            outline:none;
            border-color:#4a7cff;
            box-shadow:0 0 0 3px rgba(74,124,255,0.15);
        }

        .grades-grid{
            display:grid;
            grid-template-columns: repeat(auto-fill,minmax(180px,1fr));
            gap:14px;
            margin-top:6px;
        }

        .grade-card{
            background:#f8f9ff;
            border-radius:10px;
            padding:12px;
            border:1px solid #e3e6ff;
        }

        .grade-card label{
            font-size:13px;
            margin-bottom:4px;
            color:#555;
        }

        button{
            width:100%;
            margin-top:20px;
            padding:13px;
            border:none;
            border-radius:10px;
            background:#4a7cff;
            color:white;
            font-size:15px;
            font-weight:600;
            cursor:pointer;
            transition:background .2s, transform .05s;
        }

        button:hover{
            background:#3a67e6;
        }

        button:active{
            transform:translateY(1px);
        }

        .error{
            background:#ffe6e6;
            color:#b30000;
            padding:10px 12px;
            border-radius:8px;
            margin-bottom:14px;
            font-size:14px;
        }

        .success{
            background:#e6fff0;
            color:#0a7a3b;
            padding:10px 12px;
            border-radius:8px;
            margin-bottom:14px;
            font-size:14px;
        }
    </style>

    <div class="auth-wrapper">

        <h1>Реєстрація абітурієнта</h1>

        <#-- Повідомлення -->
        <#if success??>
            <p class="success">Абітурієнта зареєстровано успішно!</p>
        </#if>

        <#if error??>
            <p class="error">${error}</p>
        </#if>

        <form method="post" action="/register">

            <#-- CSRF токен, не чіпати -->
            <#if _csrf??>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </#if>

            <div class="section-title">Основна інформація</div>

            <div class="form-grid">
                <div class="form-group">
                    <label>ID абітурієнта</label>
                    <input type="text" name="id" placeholder="Наприклад: A-1024" required>
                </div>

                <div class="form-group">
                    <label>Ім'я</label>
                    <input type="text" name="firstName" required>
                </div>

                <div class="form-group">
                    <label>Прізвище</label>
                    <input type="text" name="lastName" required>
                </div>
            </div>

            <div class="section-title">Оцінки за предметами</div>

            <#-- Ітерація по предметах (Subject enum) -->
            <#if subjects??>
                <div class="grades-grid">
                    <#list subjects as subject>
                        <div class="grade-card">
                            <div class="form-group" style="margin-bottom:0">
                                <label>${subject.displayName}</label>
                                <input
                                        type="number"
                                        name="grade_${subject.name()}"
                                        min="0"
                                        max="12"
                                        placeholder="0–12 або порожньо">
                            </div>
                        </div>
                    </#list>
                </div>
            </#if>

            <button type="submit">Зареєструвати</button>

        </form>

    </div>

</@layout.page>
