<#import "../layout.ftl" as layout>
<@layout.page title="Список абітурієнтів">
    <div class="header-section">
        <h1>Список зареєстрованих абітурієнтів</h1>
        <p class="subtitle">Всього зареєстровано: ${applicants?size}</p>
    </div>

    <div class="content-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Прізвище та ім'я</th>
                    <th>Дії</th>
                </tr>
            </thead>
            <tbody>
                <#list applicants as applicant>
                    <tr>
                        <td><code class="id-badge">${applicant.id()}</code></td>
                        <td>
                            <div class="user-info-cell">
                                <div class="avatar">${applicant.lastName()[0]}${applicant.firstName()[0]}</div>
                                <div>
                                    <strong>${applicant.lastName()} ${applicant.firstName()}</strong>
                                </div>
                            </div>
                        </td>
                        <td>
                            <a href="/admin/applicants/${applicant.id()}" class="view-btn">Детальніше</a>
                        </td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>

    <style>
        .header-section { margin-bottom: 24px; }
        .subtitle { color: #64748b; margin-top: 4px; }
        
        .content-card { background: #fff; border-radius: 12px; padding: 0; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px 20px; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 13px; text-transform: uppercase; }
        .data-table td { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
        
        .id-badge { background: #f1f5f9; padding: 2px 6px; border-radius: 4px; font-family: monospace; font-size: 12px; color: #475569; }
        
        .user-info-cell { display: flex; align-items: center; gap: 12px; }
        .avatar { width: 36px; height: 36px; background: #e0e7ff; color: #4338ca; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px; }
        
        .view-btn { display: inline-block; padding: 6px 12px; background: #fff; border: 1px solid #e2e8f0; border-radius: 6px; color: #1e293b; text-decoration: none; font-size: 13px; font-weight: 600; transition: all 0.2s; }
        .view-btn:hover { background: #f8fafc; border-color: #cbd5e1; }
        
        tr:hover { background-color: #f8fafc; }
    </style>
</@layout.page>
