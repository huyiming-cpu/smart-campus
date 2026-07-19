-- 为新闻设置封面图片
-- news1.jpg → 第1条新闻, news2.jpg → 第2条, 以此类推
UPDATE news SET cover_image = 'news1.jpg' WHERE id = 1;
UPDATE news SET cover_image = 'news2.jpg' WHERE id = 2;
UPDATE news SET cover_image = 'news3.jpg' WHERE id = 3;
UPDATE news SET cover_image = 'news4.jpg' WHERE id = 4;
