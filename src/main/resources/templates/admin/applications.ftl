<#import "../layout.ftl" as layout>
<@layout.page title="Керування заявками">
    <div class="header-section">
        <h1>Керування заявками</h1>
        <p class="subtitle">Пошук, фільтрація та ручне оновлення статусів заяв</p>
    </div>

    <#if success??>
        <div class="alert alert-success">${success}</div>
    </#if>
    <#if error??>
        <div class="alert alert-danger">${error}</div>
    </#if>

    <div class="filter-card">
        <form method="get" action="/admin/applications" class="filter-form">
            <div class="form-group">
                <label>Спеціальність</label>
                <select name="facultyName">
                    <option value="">Усі спеціальності</option>
                    <#list faculties as faculty>
                        <option value="${faculty.name()}" <#if (facultyName!"") == faculty.name()>selected</#if>>${faculty.name()}</option>
                    </#list>
                </select>
            </div>
            <div class="form-group">
                <label>Статус</label>
                <select name="status">
                    <option value="">Усі статуси</option>
                    <#list statuses as status>
                        <option value="${status.name()}" <#if selectedStatus?? && selectedStatus.name() == status.name()>selected</#if>>
                            ${status.displayName}
                        </option>
                    </#list>
                </select>
            </div>
            <div class="form-group">
                <label>Мін. бал</label>
                <input type="number" name="minScore" value="${minScore!''}" placeholder="100">
            </div>
            <div class="form-group">
                <label>Макс. бал</label>
                <input type="number" name="maxScore" value="${maxScore!''}" placeholder="200">
            </div>
            <div class="filter-actions">
                <button type="submit" class="btn-primary">Застосувати</button>
                <a href="/admin/applications" class="btn-ghost">Скинути</a>
                <a href="/admin/applications/export?facultyName=${facultyName!''}&status=${(selectedStatus.name())!''}&minScore=${minScore!''}&maxScore=${maxScore!''}" class="btn-primary" style="background: #059669;">Експорт (CSV)</a>
                <a href="/admin/users" class="btn-secondary">Керування користувачами</a>
            </div>
        </form>
    </div>
    
    <div class="content-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Абітурієнт</th>
                    <th>Спеціальність</th>
                    <th>Результат</th>
                    <th>Поточний статус</th>
                    <th>Історія</th>
                    <th style="width: 200px">Оновити статус</th>
                </tr>
            </thead>
            <tbody>
                <#list applications as app>
                    <tr>
                        <td>
                            <div class="applicant-cell">
                                <strong>${app.getApplicantName()}</strong>
                                <small>ID: ${app.getApplicantId()}</small>
                            </div>
                        </td>
                        <td>${app.getFacultyName()}</td>
                        <td><span class="score-badge">${app.getTotalScore()}</span></td>
                        <td><span class="status-pill status-${app.getStatusName()}">${app.getStatus()}</span></td>
                        <td>
                            <a href="/admin/history/${app.getApplicantId()}/${app.getFacultyName()}" class="btn-ghost" style="padding: 4px 8px; font-size: 11px;">Історія</a>
                        </td>
                        <td>
                            <form method="post" action="/admin/applications/status" class="inline-status-form">
                                <#if _csrf??>
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </#if>
                                <input type="hidden" name="applicantId" value="${app.getApplicantId()}">
                                <input type="hidden" name="facultyName" value="${app.getFacultyName()}">
                                <select name="status">
                                    <#list statuses as statusItem>
                                        <option value="${statusItem.name()}" <#if app.getStatusName() == statusItem.name()>selected</#if>>
                                            ${statusItem.displayName}
                                        </option>
                                    </#list>
                                </select>
                                <button type="submit" title="Зберегти"><i class="check-icon">✓</i></button>
                            </form>
                        </td>
                    </tr>
                <#else>
                    <tr>
                        <td colspan="6" class="empty">Заявок за обраними умовами немає.</td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>

    <style>
        .header-section { margin-bottom: 24px; }
        .subtitle { color: #64748b; margin-top: 4px; }

        .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-weight: 500; font-size: 14px; }
        .alert-success { background: #dcfce7; color: #166534; border: 1px solid #bbf7d0; }
        .alert-danger { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }

        .filter-card { background: #fff; border-radius: 12px; padding: 20px; margin-bottom: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .filter-form { display: grid; grid-template-columns: 1fr 1fr auto; gap: 20px; align-items: end; }
        .form-group label { display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 6px; }
        .form-group select { width: 100%; padding: 8px 12px; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 14px; }
        .filter-actions { display: flex; gap: 8px; }

        .btn-primary { background: #2563eb; color: #fff; border: none; padding: 9px 16px; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 14px; }
        .btn-ghost { background: #f1f5f9; color: #475569; text-decoration: none; padding: 9px 16px; border-radius: 6px; font-weight: 600; font-size: 14px; }

        .content-card { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px 20px; background: #f8fafc; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; }
        .data-table td { padding: 14px 20px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }

        .applicant-cell { display: flex; flex-direction: column; }
        .applicant-cell strong { color: #1e293b; }
        .applicant-cell small { color: #64748b; font-family: monospace; }

        .score-badge { background: #f1f5f9; color: #1e293b; font-weight: 700; padding: 4px 8px; border-radius: 6px; }

        .status-pill { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
        .status-PENDING { background: #fef3c7; color: #92400e; }
        .status-ADMITTED { background: #dcfce7; color: #166534; }
        .status-REJECTED { background: #fee2e2; color: #991b1b; }

        .inline-status-form { display: flex; gap: 4px; }
        .inline-status-form select { flex: 1; padding: 4px 8px; border: 1px solid #e2e8f0; border-radius: 4px; font-size: 13px; }
        .inline-status-form button { background: #f1f5f9; border: 1px solid #e2e8f0; color: #1e293b; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .inline-status-form button:hover { background: #e2e8f0; }

        .empty { text-align: center; color: #94a3b8; padding: 40px; }
    </style>
</@layout.page>
