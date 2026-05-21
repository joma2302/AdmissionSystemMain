<#import "../layout.ftl" as layout>
<@layout.page title="Деталі абітурієнта">
    <div class="header-section">
        <a href="/admin/applicants" class="back-link">← Назад до списку</a>
        <h1>Профіль абітурієнта</h1>
    </div>

    <#if applicant??>
        <div class="details-grid">
            <div class="content-card profile-info">
                <div class="profile-header">
                    <div class="avatar-lg">${applicant.lastName()[0]}${applicant.firstName()[0]}</div>
                    <div class="profile-title">
                        <h2>${applicant.lastName()} ${applicant.firstName()}</h2>
                        <span class="id-badge">ID: ${applicant.id()}</span>
                    </div>
                </div>
                
                <div class="info-section">
                    <h3>Контактна та особиста інформація</h3>
                    <div class="info-row">
                        <span class="label">Прізвище:</span>
                        <span class="value">${applicant.lastName()}</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Ім'я:</span>
                        <span class="value">${applicant.firstName()}</span>
                    </div>
                </div>
            </div>

            <div class="content-card grades-info">
                <h3>Оцінки атестата та ЗНО/НМТ</h3>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Предмет</th>
                            <th>Бал</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list applicant.grades()?keys as subject>
                            <tr>
                                <td>${subject}</td>
                                <td><strong class="score">${applicant.grades()[subject]}</strong></td>
                            </tr>
                        <#else>
                            <tr>
                                <td colspan="2" class="empty">Оцінки не додані</td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>

            <div class="content-card applications-info">
                <h3>Подані заявки</h3>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Факультет/Спеціальність</th>
                            <th>Загальний бал</th>
                            <th>Статус</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list applicant.applications() as app>
                            <tr>
                                <td><strong>${app.facultyName()}</strong></td>
                                <td><span class="total-score">${app.totalScore()}</span></td>
                                <td><span class="status-pill status-${app.statusName()}">${app.status()}</span></td>
                            </tr>
                        <#else>
                            <tr>
                                <td colspan="3" class="empty">Заявки не знайдені</td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
    <#else>
        <div class="alert alert-danger">
            Абітурієнта з таким ID не знайдено.
        </div>
    </#if>

    <style>
        .header-section { margin-bottom: 24px; }
        .back-link { color: #64748b; text-decoration: none; font-size: 14px; display: inline-block; margin-bottom: 8px; }
        .back-link:hover { color: #2563eb; }

        .details-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
        .profile-info { grid-column: span 2; display: flex; flex-direction: column; gap: 20px; }
        
        .profile-header { display: flex; align-items: center; gap: 24px; padding-bottom: 20px; border-bottom: 1px solid #f1f5f9; }
        .avatar-lg { width: 80px; height: 80px; background: #e0e7ff; color: #4338ca; border-radius: 20px; display: flex; align-items: center; justify-content: center; font-size: 32px; font-weight: 800; }
        .profile-title h2 { margin: 0; font-size: 24px; }
        .id-badge { display: inline-block; margin-top: 4px; background: #f1f5f9; padding: 2px 8px; border-radius: 6px; font-family: monospace; color: #64748b; }

        .content-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .content-card h3 { font-size: 16px; margin: 0 0 16px 0; color: #475569; border-bottom: 2px solid #f1f5f9; padding-bottom: 8px; }

        .info-row { display: flex; padding: 12px 0; border-bottom: 1px solid #f8fafc; }
        .info-row .label { width: 200px; color: #64748b; font-size: 14px; }
        .info-row .value { color: #1e293b; font-weight: 600; }

        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 10px; background: #f8fafc; font-size: 12px; text-transform: uppercase; color: #64748b; }
        .data-table td { padding: 12px 10px; border-bottom: 1px solid #f1f5f9; }

        .score { color: #2563eb; }
        .total-score { font-weight: 800; font-size: 16px; color: #1e293b; }

        .status-pill { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
        .status-PENDING { background: #fef3c7; color: #92400e; }
        .status-ADMITTED { background: #dcfce7; color: #166534; }
        .status-REJECTED { background: #fee2e2; color: #991b1b; }

        .empty { text-align: center; color: #94a3b8; padding: 20px; }
        .alert { padding: 20px; border-radius: 8px; background: #fee2e2; color: #991b1b; }

        @media (max-width: 768px) {
            .details-grid { grid-template-columns: 1fr; }
            .profile-info { grid-column: span 1; }
        }
    </style>
</@layout.page>
