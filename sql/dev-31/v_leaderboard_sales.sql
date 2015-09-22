DROP VIEW IF EXISTS leaderboard_sales;
CREATE VIEW leaderboard_sales AS
SELECT 
	r.id, 
    r.rank_fetch_id, 
    r.position, 
    r.itemid as rank_item_id,
    r.price, 
    r.currency, 
    r.revenue_iphone,
    r.revenue_ipad, 
    r.revenue_universal, 
    r.downloads_iphone, 
    r.downloads_ipad, 
    r.downloads_universal, 

    rf.group_fetch_code, 
    rf.fetch_date, 
    rf.country, 
    rf.category, 
    rf.type, 
    rf.platform, 
	
    da.developer_name,
    ss.dataaccountid, 
    ss.itemid, 
    ss.title, 

    ss.iphone_app_revenue, 
    ss.ipad_app_revenue, 
    ss.universal_app_revenue, 
    ss.total_app_revenue, 

    ss.iphone_iap_revenue,
    ss.ipad_iap_revenue,
    ss.iap_revenue as total_iap_revenue, 

    ss.iphone_downloads,
    ss.ipad_downloads, 
    ss.universal_downloads, 
    ss.total_downloads, 

    ss.iphone_updates, 
    ss.ipad_updates, 
    ss.universal_updates, 
    ss.total_updates, 

    ss.total_download_and_updates, 

    ss.free_subs_count, 
    ss.paid_subs_count, 
    ss.total_subs_count, 

    ss.subs_revenue
FROM
    rank_fetch rf 
    INNER JOIN `rank2` r ON (r.rank_fetch_id = rf.rank_fetch_id and fetch_time>'22:00')
    LEFT JOIN sale_summary ss on (r.itemid=ss.itemid and rf.fetch_date=ss.date and ss.country=rf.country)
    LEFT JOIN dataaccount da on (ss.dataaccountid=da.id);