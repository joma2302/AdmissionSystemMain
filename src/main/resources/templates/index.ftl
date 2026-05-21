<#import "layout.ftl" as layout>
<@layout.page title="Приймальна комісія — Головна">

    <div class="home-wrapper">
        <div class="home-header">
            <h1>Система «Приймальна комісія»</h1>
            <p>Ласкаво просимо! Оберіть дію нижче.</p>
        </div>

        <div class="actions">
            <#if !isAuthenticated>
                <a href="/login" class="action-card">
                    <div class="card-title">Увійти в систему</div>
                    <div class="card-desc">Авторизуйтесь для подачі заявок</div>
                </a>
                <a href="/signup" class="action-card" style="background: #10b981;">
                    <div class="card-title">Реєстрація</div>
                    <div class="card-desc">Створити новий обліковий запис</div>
                </a>
            </#if>

            <#if isAuthenticated>
                <#assign isAdmin = false>
                <#if auth.authorities??>
                    <#list auth.authorities as authItem>
                        <#if authItem.authority == "ROLE_ADMIN">
                            <#assign isAdmin = true>
                        </#if>
                    </#list>
                </#if>

                <#if isAdmin>
                    <#-- Картки для адміністратора -->
                    <a href="/admin" class="action-card admin-card">
                        <div class="card-title">Панель керування</div>
                        <div class="card-desc">Статистика та моніторинг вступу</div>
                    </a>

                    <a href="/admin/applications" class="action-card admin-card">
                        <div class="card-title">Керування заявками</div>
                        <div class="card-desc">Перегляд, фільтрація та зміна статусів</div>
                    </a>

                    <a href="/admin/faculties" class="action-card admin-card">
                        <div class="card-title">Факультети</div>
                        <div class="card-desc">Налаштування місць та предметних вимог</div>
                    </a>

                    <a href="/admin/applicants" class="action-card admin-card">
                        <div class="card-title">База абітурієнтів</div>
                        <div class="card-desc">Список усіх зареєстрованих осіб</div>
                    </a>

                    <a href="/admission" class="action-card admin-card primary-admin">
                        <div class="card-title">Провести зарахування</div>
                        <div class="card-desc">Автоматичний розподіл місць за рейтингом</div>
                    </a>
                <#else>
                    <#-- Картки для абітурієнта -->
                    <a href="/register" class="action-card">
                        <div class="card-title">Мій профіль</div>
                        <div class="card-desc">Перегляд та редагування особистих даних</div>
                    </a>

                    <a href="/apply" class="action-card">
                        <div class="card-title">Подати заявку</div>
                        <div class="card-desc">Обрати факультет та подати документи</div>
                    </a>

                    <a href="/applications" class="action-card">
                        <div class="card-title">Мої заявки</div>
                        <div class="card-desc">Відстеження статусу поданих заяв</div>
                    </a>

                    <a href="/upload" class="action-card">
                        <div class="card-title">Документи</div>
                        <div class="card-desc">Завантажити скани документів</div>
                    </a>
                </#if>

                <a href="/ranking" class="action-card <#if isAdmin>admin-card</#if>">
                    <div class="card-title">Рейтингові списки</div>
                    <div class="card-desc">Поточна ситуація по всіх факультетах</div>
                </a>
            </#if>
        </div>
    </div>

    <style>
        .admin-card { background: #334155 !important; }
        .admin-card:hover { background: #1e293b !important; box-shadow:0 10px 26px rgba(51,65,85,0.3) !important; }
        .primary-admin { background: #2563eb !important; }
        .primary-admin:hover { background: #1d4ed8 !important; }
        
        .home-wrapper{
            max-width:1000px;
            margin:40px auto;
            padding:40px;
            background:#fff;
            border-radius:16px;
            box-shadow:0 14px 40px rgba(0,0,0,0.05);
            text-align:center;
        }

        .home-header h1{
            color:#1e293b;
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom:12px;
            letter-spacing: -0.025em;
        }

        .home-header p{
            color:#64748b;
            font-size: 1.125rem;
            margin-bottom:48px;
        }

        .actions{
            display:grid;
            grid-template-columns: repeat(auto-fit,minmax(280px,1fr));
            gap:24px;
        }

        .action-card{
            display:flex;
            flex-direction: column;
            justify-content: center;
            background:#3b82f6;
            color:#fff;
            padding:32px 24px;
            border-radius:16px;
            text-decoration:none;
            transition: all .2s cubic-bezier(0.4, 0, 0.2, 1);
            text-align:left;
            position:relative;
            overflow:hidden;
            border: 1px solid rgba(255,255,255,0.1);
        }

        .action-card:hover{
            transform:translateY(-4px);
            box-shadow:0 12px 24px rgba(59,130,246,0.25);
        }

        .action-card:active{
            transform:translateY(-1px);
        }

        .card-title{
            font-size:1.125rem;
            font-weight:700;
            margin-bottom:8px;
            z-index: 1;
        }

        .card-desc{
            font-size:0.875rem;
            opacity:.85;
            line-height:1.5;
            z-index: 1;
        }

        .action-card::after{
            content:"";
            position:absolute;
            bottom:-20px;
            right:-20px;
            width:100px;
            height:100px;
            background:rgba(255,255,255,0.1);
            border-radius:50%;
            transition: all .3s;
        }

        .action-card:hover::after {
            transform: scale(1.5);
            background:rgba(255,255,255,0.15);
        }
    </style>

</@layout.page>
