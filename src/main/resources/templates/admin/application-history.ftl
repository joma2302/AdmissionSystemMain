<#import "../layout.ftl" as layout>
<@layout.page title="Історія статусів">
    <div class="header-section">
        <h1>Історія статусів</h1>
        <p class="subtitle">Заявка: ${applicantId} на ${facultyName}</p>
    </div>

    <div class="content-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Статус</th>
                    <th>Дата та час</th>
                </tr>
            </thead>
            <tbody>
                <#list history as entry>
                    <tr>
                        <td><span class="status-pill status-${entry.status.name()}">${entry.status.displayName}</span></td>
                        <td>${entry.changedAt}</td>
                    </tr>
                <#else>
                    <tr>
                        <td colspan="2" class="empty">Історія змін відсутня.</td>
                    </tr>
                </#list>
            </tbody>
        </table>
        <div class="actions" style="margin-top: 20px;">
            <a href="/admin/applications" class="btn-ghost">Назад до списку</a>
        </div>
    </div>
    
    <style>
        .content-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px 20px; background: #f8fafc; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 12px; text-transform: uppercase; }
        .data-table td { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; }
        
        .status-pill { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
        .status-PENDING { background: #fef3c7; color: #92400e; }
        .status-ADMITTED { background: #dcfce7; color: #166534; }
        .status-REJECTED { background: #fee2e2; color: #991b1b; }
        
        .btn-ghost { background: #f1f5f9; color: #475569; text-decoration: none; padding: 9px 16px; border-radius: 6px; font-weight: 600; font-size: 14px; }
        .empty { text-align: center; color: #94a3b8; padding: 40px; }
    </style>
</@layout.page>
