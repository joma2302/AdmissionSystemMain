<#-- Базовий макрос-шаблон сторінки (Template Method pattern) -->
<#macro page title="Приймальна комісія">
    <!DOCTYPE html>
    <html lang="uk">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${title}</title>

        <style>
            body{
                font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
                margin:0;
                padding:0;
                background:#f3f5f9;
                color:#1f2937;
            }

            /* NAVBAR */
            .navbar{
                background:#1e293b;
                padding:0 28px;
                display:flex;
                justify-content:space-between;
                align-items:center;
                box-shadow:0 4px 18px rgba(0,0,0,0.1);
                height: 64px;
                position: sticky;
                top: 0;
                z-index: 1000;
            }

            .nav-brand {
                font-weight: 800;
                font-size: 1.25rem;
                color: #f8fafc;
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .nav-links{
                display:flex;
                align-items:center;
                gap:4px;
                height: 100%;
            }

            .nav-links a{
                color:#94a3b8;
                text-decoration:none;
                font-size:14px;
                font-weight:500;
                padding: 0 16px;
                height: 100%;
                display: flex;
                align-items: center;
                transition: all .2s;
            }

            .nav-links a:hover{
                color:#f8fafc;
                background: rgba(255,255,255,0.05);
            }

            .nav-links a.active {
                color: #3b82f6;
                border-bottom: 2px solid #3b82f6;
            }

            .user-info{
                display:flex;
                align-items:center;
                gap:16px;
                color:#f8fafc;
                font-size:14px;
            }

            .role-badge{
                background:#334155;
                color: #94a3b8;
                padding:4px 10px;
                border-radius:6px;
                font-size:11px;
                font-weight:700;
                text-transform: uppercase;
                letter-spacing: 0.025em;
                border: 1px solid #475569;
            }

            .role-admin {
                background: rgba(239, 68, 68, 0.1);
                color: #f87171;
                border-color: rgba(239, 68, 68, 0.2);
            }

            .logout-btn{
                background:#ef4444;
                border:none;
                color:white;
                cursor:pointer;
                font-size:13px;
                padding: 6px 12px;
                border-radius: 6px;
                font-weight: 600;
                transition: background .2s;
            }

            .logout-btn:hover{
                background:#dc2626;
            }

            .login-link {
                color: #3b82f6;
                text-decoration: none;
                font-weight: 600;
                padding: 8px 16px;
                border: 1px solid #3b82f6;
                border-radius: 8px;
                transition: all 0.2s;
            }

            .login-link:hover {
                background: #3b82f6;
                color: white;
            }

            /* PAGE CONTAINER */
            .container{
                max-width:1000px;
                margin:34px auto;
                padding:26px 28px;
                background:#fff;
                border-radius:14px;
                box-shadow:0 10px 30px rgba(0,0,0,0.08);
            }

            h1{
                margin-top:0;
                margin-bottom:18px;
            }

            /* TABLES */
            table{
                width:100%;
                border-collapse:collapse;
                margin-top:16px;
                overflow:hidden;
                border-radius:10px;
            }

            th{
                background:#2563eb;
                color:white;
                padding:12px;
                font-size:14px;
            }

            td{
                padding:12px;
                border-bottom:1px solid #eee;
                font-size:14px;
            }

            tr:nth-child(even) td{
                background:#fafbff;
            }

            tr:hover td{
                background:#eef2ff;
            }

            /* FORM */
            input, select{
                padding:10px 12px;
                margin:6px 0;
                border:1px solid #ccc;
                border-radius:8px;
                width:100%;
                box-sizing:border-box;
                font-size:14px;
                transition:border .2s, box-shadow .2s;
            }

            input:focus, select:focus{
                outline:none;
                border-color:#3b82f6;
                box-shadow:0 0 0 3px rgba(59,130,246,0.15);
            }

            button{
                padding:11px 18px;
                background:#2563eb;
                color:white;
                border:none;
                border-radius:10px;
                cursor:pointer;
                font-size:14px;
                font-weight:600;
                transition:background .18s, transform .05s;
            }

            button:hover{
                background:#1d4ed8;
            }

            button:active{
                transform:translateY(1px);
            }

            /* MESSAGES */
            .success{
                background:#e6fff0;
                color:#0a7a3b;
                padding:10px 12px;
                border-radius:8px;
                margin-bottom:12px;
                font-weight:500;
            }

            .error{
                background:#ffe6e6;
                color:#b30000;
                padding:10px 12px;
                border-radius:8px;
                margin-bottom:12px;
                font-weight:500;
            }

            .form-group{ margin-bottom:14px; }
            label{ font-size:14px; font-weight:600; margin-bottom:4px; display:block; }

        </style>
    </head>
    <body>

    <#if currentUser??>
        <#global auth = currentUser>
        <#global isAuthenticated = true>
    <#else>
        <#global auth = {}>
        <#global isAuthenticated = false>
    </#if>

    <div class="navbar">
        <div class="nav-links">
            <a href="/" class="nav-brand">Вступ 2026</a>
            <a href="/">Головна</a>

            <#-- Навігація для авторизованих користувачів -->
            <#if isAuthenticated>
                <#assign isPrivileged = false>
                <#if auth.authorities??>
                    <#list auth.authorities as authItem>
                        <#if authItem.authority == "ROLE_ADMIN" || authItem.authority == "ROLE_MANAGER">
                            <#assign isPrivileged = true>
                        </#if>
                    </#list>
                </#if>

                <#if isPrivileged>
                    <a href="/admin">Адмін-панель</a>
                    <a href="/admin/applications">Заявки</a>
                    <a href="/admin/faculties">Факультети</a>
                    <a href="/admission">Зарахування</a>
                <#else>
                    <a href="/register">Мій профіль</a>
                    <a href="/apply">Подати заявку</a>
                    <a href="/applications">Мої заявки</a>
                </#if>
                <a href="/ranking">Загальний рейтинг</a>
            </#if>
        </div>

        <div class="user-info">
            <#if isAuthenticated>
                <span>${(auth.name)!"User"}</span>

                <#if auth.authorities??>
                    <#list auth.authorities as authItem>
                        <span class="role-badge <#if authItem.authority == "ROLE_ADMIN" || authItem.authority == "ROLE_MANAGER">role-admin</#if>">
                            ${authItem.authority?replace("ROLE_", "")}
                        </span>
                    </#list>
                </#if>

                <form method="post" action="/logout" style="display:inline;">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>
                    <button type="submit" class="logout-btn">Вихід</button>
                </form>
            <#else>
                <a href="/login" class="login-link">Увійти</a>
            </#if>
        </div>
    </div>

    <div class="container">
        <#nested>
    </div>

    </body>
    </html>
</#macro>
