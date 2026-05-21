<#-- Сторінка реєстрації нового користувача -->
<#import "layout.ftl" as layout>
<@layout.page title="Реєстрація користувача">

    <style>
        .auth-wrapper{
            max-width: 420px;
            margin: 40px auto;
            padding: 28px 26px;
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        .auth-wrapper h1{
            margin-bottom: 18px;
            font-size: 26px;
            text-align: center;
        }

        .form-group{
            display: flex;
            flex-direction: column;
            margin-bottom: 16px;
        }

        .form-group label{
            font-size: 14px;
            margin-bottom: 6px;
            color: #444;
        }

        .form-group input,
        .form-group select{
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 15px;
            transition: border .2s, box-shadow .2s;
        }

        .form-group input:focus,
        .form-group select:focus{
            outline: none;
            border-color: #4a7cff;
            box-shadow: 0 0 0 3px rgba(74,124,255,0.15);
        }

        button{
            width: 100%;
            margin-top: 8px;
            padding: 12px;
            border: none;
            border-radius: 10px;
            background: #4a7cff;
            color: white;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: background .2s, transform .05s, box-shadow .1s;
        }

        button:hover{
            background: #3a67e6;
        }

        button:active{
            transform: translateY(1px);
            box-shadow: 0 3px 10px rgba(0,0,0,0.15) inset;
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
    </style>

    <div class="auth-wrapper">

        <h1>Реєстрація в системі</h1>

        <#-- Повідомлення про помилки -->
        <#if error??>
            <p class="error">${error}</p>
        </#if>

        <#-- Повідомлення про успіх -->
        <#if success??>
            <p class="success">${success}</p>
        </#if>

        <form method="post" action="/signup">

            <#-- CSRF токен (не прибирати, інакше безпека плаче) -->
            <#if _csrf??>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </#if>

            <div class="form-group">
                <label for="username">Ім'я користувача</label>
                <input id="username" type="text" name="username" placeholder="Введіть логін" required>
            </div>

            <div class="form-group">
                <label for="password">Пароль</label>
                <input id="password" type="password" name="password" placeholder="Мінімум 6 символів" required>
            </div>

            <div class="form-group">
                <label for="role">Роль</label>
                <select id="role" name="role" required>
                    <option value="APPLICANT">Абітурієнт</option>
                    <option value="ADMIN">Адміністратор</option>
                </select>
            </div>

            <button type="submit">Зареєструватися</button>
        </form>

        <div class="auth-footer">
            Вже є акаунт? <a href="/login">Увійти</a>
        </div>

    </div>

</@layout.page>
