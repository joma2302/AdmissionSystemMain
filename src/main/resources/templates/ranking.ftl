<#-- Сторінка рейтингу заявок на факультет -->
<#import "layout.ftl" as layout>
<@layout.page title="Рейтинг заявок">

    <style>
        .page-wrapper{
            max-width: 900px;
            margin: 40px auto;
            padding: 28px;
            background:#fff;
            border-radius:14px;
            box-shadow:0 12px 32px rgba(0,0,0,0.08);
        }

        h1{
            text-align:center;
            margin-bottom:22px;
        }

        .filter-box{
            display:flex;
            gap:12px;
            align-items:end;
            margin-bottom:22px;
            flex-wrap:wrap;
            padding:16px;
            background:#f8f9ff;
            border-radius:10px;
            border:1px solid #e6e8ff;
        }

        .form-group{
            display:flex;
            flex-direction:column;
            min-width:240px;
        }

        label{
            font-size:14px;
            margin-bottom:6px;
            color:#444;
        }

        select{
            padding:10px 12px;
            border-radius:8px;
            border:1px solid #ccc;
            font-size:15px;
            transition:border .2s, box-shadow .2s;
        }

        select:focus{
            outline:none;
            border-color:#4a7cff;
            box-shadow:0 0 0 3px rgba(74,124,255,0.15);
        }

        button{
            padding:11px 18px;
            border:none;
            border-radius:10px;
            background:#4a7cff;
            color:white;
            font-weight:600;
            cursor:pointer;
            transition:background .2s, transform .05s;
            height:40px;
        }

        button:hover{
            background:#3a67e6;
        }

        button:active{
            transform:translateY(1px);
        }

        table{
            width:100%;
            border-collapse:collapse;
            margin-top:10px;
            overflow:hidden;
            border-radius:10px;
        }

        th{
            text-align:left;
            padding:12px;
            background:#4a7cff;
            color:white;
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

        .empty{
            text-align:center;
            padding:30px 10px;
            color:#666;
            font-size:15px;
        }

        .status{
            padding:4px 10px;
            border-radius:20px;
            font-size:13px;
            font-weight:600;
            display:inline-block;
        }

        .status-APPROVED{ background:#e6fff0; color:#0a7a3b; }
        .status-PENDING{ background:#fff5e6; color:#a86500; }
        .status-REJECTED{ background:#ffe6e6; color:#b30000; }
    </style>

    <div class="page-wrapper">

        <h1>Рейтинг заявок на факультет</h1>

        <form method="get" action="/ranking" class="filter-box">
            <div class="form-group">
                <label>Факультет</label>
                <select name="facultyName" required>
                    <option value="">-- Оберіть факультет --</option>
                    <#if faculties??>
                        <#list faculties as faculty>
                            <option value="${faculty.name}"
                                    <#if (facultyName!"") == faculty.name>selected</#if>>
                                ${faculty.name}
                            </option>
                        </#list>
                    </#if>
                </select>
            </div>
            <button type="submit">Показати рейтинг</button>
        </form>

        <#if ranked??>
            <#if ranked?size == 0>

                <div class="empty">
                    Заявок не знайдено для обраного факультету.
                </div>

            <#else>

                <table>
                    <tr>
                        <th>№</th>
                        <th>Абітурієнт</th>
                        <th>Бали</th>
                        <th>Статус</th>
                    </tr>

                    <#list ranked as app>
                        <tr>
                            <td>${app?index + 1}</td>
                            <td>${app.applicant.fullName}</td>
                            <td><strong>${app.totalScore}</strong></td>
                            <td>
                            <span class="status status-${app.status}">
                                ${app.status}
                            </span>
                            </td>
                        </tr>
                    </#list>
                </table>

            </#if>
        </#if>

    </div>

</@layout.page>

