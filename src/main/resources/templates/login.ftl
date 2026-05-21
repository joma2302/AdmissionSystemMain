<#-- Сторінка входу до системи -->
<#import "layout.ftl" as layout>
<@layout.page title="Вхід">

    <style>
        .auth-wrapper{
            max-width:420px;
            margin:60px auto;
            padding:30px 28px;
            background:#fff;
            border-radius:14px;
            box-shadow:0 12px 32px rgba(0,0,0,0.08);
        }

        h1{
            text-align:center;
            margin-bottom:22px;
        }

        .form-group{
            display:flex;
            flex-direction:column;
            margin-bottom:16px;
        }

        label{
            font-size:14px;
            margin-bottom:6px;
            color:#444;
        }

        input{
            padding:11px 12px;
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

        button{
            width:100%;
            margin-top:6px;
            padding:12px;
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

        .auth-footer{
            text-align:center;
            margin-top:18px;
            font-size:14px;
            color:#555;
        }

        .auth-footer a{
            color:#4a7cff;
            text-decoration:none;
            font-weight:600;
        }

        .auth-footer a:hover{
            text-decoration:underline;
        }

        .security-note{
            margin-top:14px;
            font-size:12px;
            color:#777;
            text-align:center;
        }
    </style>

    <div class="auth-wrapper">

        <h1>Вхід до системи</h1>

        <#-- Повідомлення -->
        <#if error??>
            <p class="error">${error}</p>
        </#if>

        <#if success??>
            <p class="success">${success}</p>
        </#if>

        <#-- Форма входу обробляється Spring Security -->
        <form method="post" action="/login">

            <#-- CSRF токен (не чіпати, без нього Spring почне плакати) -->
            <#if _csrf??>
                <input type="hidden" name="${(_csrf.parameterName)!'_csrf'}" value="${(_csrf.token)!''}">
            <#else>
                <!-- CSRF is missing! -->
            </#if>

            <div class="form-group">
                <label for="username">Ім'я користувача</label>
                <input id="username" type="text" name="username" autocomplete="username" required>
            </div>

            <div class="form-group">
                <label for="password">Пароль</label>
                <input id="password" type="password" name="password" autocomplete="current-password" required>
            </div>

            <button type="submit">Увійти</button>
        </form>

        <div class="auth-footer">
            Немає акаунту? <a href="/signup">Зареєструватися</a>
        </div>

        <div class="security-note">
            Дані передаються через захищене з’єднання
        </div>

    </div>

</@layout.page>
