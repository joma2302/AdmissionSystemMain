<#import "../layout.ftl" as layout>
<@layout.page title="Керування користувачами">
    <div class="header-section">
        <h1>Керування користувачами</h1>
        <p class="subtitle">Скидання паролів та адміністрування облікових записів</p>
    </div>

    <#if RequestParameters?? && RequestParameters.success??>
        <div class="alert alert-success">Пароль успішно змінено.</div>
    </#if>

    <div class="content-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Ім'я користувача</th>
                    <th>Роль</th>
                    <th>Дії</th>
                </tr>
            </thead>
            <tbody>
                <#list users as user>
                    <tr>
                        <td><strong>${user.username}</strong></td>
                        <td><span class="badge">${user.role}</span></td>
                        <td>
                            <form action="/admin/users/reset-password" method="post" class="inline-form">
                                <#if _csrf??>
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </#if>
                                <input type="hidden" name="username" value="${user.username}">
                                <input type="password" name="newPassword" placeholder="Новий пароль" class="password-input" required>
                                <button type="submit" class="btn-warning">Скинути пароль</button>
                            </form>
                        </td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>

    <style>
        .header-section { margin-bottom: 24px; }
        .subtitle { color: #64748b; margin-top: 4px; }
        
        .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-weight: 500; font-size: 14px; background: #dcfce7; color: #166534; border: 1px solid #bbf7d0; }

        .content-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px 20px; background: #f8fafc; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; }
        .data-table td { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; }
        
        .badge { background: #e2e8f0; color: #475569; padding: 4px 8px; border-radius: 6px; font-weight: 600; font-size: 11px; text-transform: uppercase; }

        .inline-form { display: flex; gap: 8px; align-items: center; }
        .password-input { padding: 6px 12px; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 13px; }
        .btn-warning { background: #f59e0b; color: #fff; border: none; padding: 6px 12px; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; }
        .btn-warning:hover { background: #d97706; }
    </style>
</@layout.page>
