<#import "../layout.ftl" as layout>
<@layout.page title="Керування факультетами">
    <div class="header-section">
        <h1>Керування спеціальностями</h1>
        <p class="subtitle">Налаштування лімітів місць та вимог до вступних балів</p>
    </div>

    <div class="admin-grid">
        <div class="content-card form-section">
            <h2>Додати спеціальність</h2>
            <form method="post" action="/admin/faculties/add" class="vertical-form">
                <#if _csrf??>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </#if>
                <div class="form-group">
                    <label>Назва спеціальності</label>
                    <input type="text" name="name" placeholder="Наприклад: Комп'ютерні науки" required>
                </div>
                <div class="form-group">
                    <label>Ліміт місць (ліцензійний обсяг)</label>
                    <input type="number" name="maxStudents" placeholder="Кількість місць" required min="1">
                </div>
                <button type="submit" class="btn-primary full-width">Створити спеціальність</button>
            </form>
        </div>

        <div class="content-card table-section">
            <h2>Список спеціальностей та вимоги</h2>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Спеціальність</th>
                        <th>Місць</th>
                        <th>Вимоги до балів</th>
                        <th>Дії</th>
                    </tr>
                </thead>
                <tbody>
                    <#list faculties as f>
                        <tr>
                            <td>
                                <strong>${f.name()}</strong>
                                <div class="demand-info">Заявок: ${f.applicationCount()} | Конкурс: ${f.competition()?string["0.00"]}</div>
                            </td>
                            <td><span class="seats-badge">${f.maxStudents()}</span></td>
                            <td>
                                <div class="requirements-container">
                                    <#list f.requirements() as req>
                                        <div class="req-tag">
                                            ${req.subject()}: <strong>${req.minimumScore()}</strong>
                                        </div>
                                    <#else>
                                        <span class="no-req">Вимоги не встановлені</span>
                                    </#list>
                                    
                                    <div class="add-req-trigger">
                                        <button class="btn-icon-sm" onclick="this.nextElementSibling.style.display='flex'; this.style.display='none'">+ додати вимогу</button>
                                        <form method="post" action="/admin/faculties/requirements/add" class="mini-req-form" style="display:none">
                                            <#if _csrf??>
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                            </#if>
                                            <input type="hidden" name="facultyName" value="${f.name()}">
                                            <select name="subject" required>
                                                <#list subjects as s>
                                                    <option value="${s.name()}">${s.displayName()}</option>
                                                </#list>
                                            </select>
                                            <input type="number" name="minimumScore" required min="100" max="200" value="100">
                                            <button type="submit">OK</button>
                                        </form>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="actions-dropdown">
                                    <form method="post" action="/admin/faculties/requirements/clear" style="display:inline">
                                        <#if _csrf??>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                        </#if>
                                        <input type="hidden" name="facultyName" value="${f.name()}">
                                        <button type="submit" class="btn-outline-warning" title="Очистити всі вимоги">Очистити</button>
                                    </form>
                                    <form method="post" action="/admin/faculties/delete" style="display:inline">
                                        <#if _csrf??>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                        </#if>
                                        <input type="hidden" name="name" value="${f.name()}">
                                        <button type="submit" class="btn-outline-danger" onclick="return confirm('Видалити спеціальність?')">Видалити</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <#else>
                        <tr>
                            <td colspan="4" class="empty">Спеціальності ще не створені.</td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>

    <style>
        .header-section { margin-bottom: 24px; }
        .subtitle { color: #64748b; margin-top: 4px; }

        .admin-grid { display: grid; grid-template-columns: 350px 1fr; gap: 24px; align-items: start; }
        
        .content-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .content-card h2 { font-size: 18px; margin-bottom: 20px; color: #1e293b; }

        .vertical-form .form-group { margin-bottom: 16px; }
        .vertical-form label { display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 6px; }
        .vertical-form input { width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px; box-sizing: border-box; }
        .full-width { width: 100%; }

        .btn-primary { background: #2563eb; color: #fff; border: none; padding: 12px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        .btn-primary:hover { background: #1d4ed8; }

        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px; background: #f8fafc; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 12px; text-transform: uppercase; }
        .data-table td { padding: 16px 12px; border-bottom: 1px solid #f1f5f9; vertical-align: top; }

        .demand-info { font-size: 11px; color: #64748b; margin-top: 4px; }
        .seats-badge { background: #f1f5f9; color: #1e293b; font-weight: 700; padding: 4px 10px; border-radius: 6px; }

        .requirements-container { display: flex; flex-wrap: wrap; gap: 8px; }
        .req-tag { background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; padding: 4px 8px; border-radius: 6px; font-size: 12px; }
        .no-req { color: #94a3b8; font-style: italic; font-size: 13px; }

        .add-req-trigger { width: 100%; margin-top: 8px; }
        .btn-icon-sm { background: none; border: 1px dashed #cbd5e1; color: #64748b; padding: 4px 8px; border-radius: 4px; font-size: 11px; cursor: pointer; }
        .mini-req-form { gap: 4px; }
        .mini-req-form select, .mini-req-form input { padding: 2px 4px; font-size: 12px; border: 1px solid #e2e8f0; border-radius: 4px; }
        .mini-req-form button { background: #2563eb; color: #fff; border: none; padding: 2px 8px; border-radius: 4px; font-size: 12px; cursor: pointer; }

        .actions-dropdown { display: flex; gap: 8px; }
        .btn-outline-warning { background: transparent; border: 1px solid #fbbf24; color: #b45309; padding: 4px 8px; border-radius: 6px; font-size: 11px; font-weight: 600; cursor: pointer; }
        .btn-outline-danger { background: transparent; border: 1px solid #fca5a5; color: #b91c1c; padding: 4px 8px; border-radius: 6px; font-size: 11px; font-weight: 600; cursor: pointer; }
        .btn-outline-danger:hover { background: #fee2e2; }

        .empty { text-align: center; color: #94a3b8; padding: 40px; }

        @media (max-width: 1024px) {
            .admin-grid { grid-template-columns: 1fr; }
        }
    </style>
</@layout.page>
