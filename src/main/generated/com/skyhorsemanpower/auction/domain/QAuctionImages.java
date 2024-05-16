package com.skyhorsemanpower.auction.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuctionImages is a Querydsl query type for AuctionImages
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionImages extends EntityPathBase<AuctionImages> {

    private static final long serialVersionUID = -414276298L;

    public static final QAuctionImages auctionImages = new QAuctionImages("auctionImages");

    public final NumberPath<Long> auctionImagesId = createNumber("auctionImagesId", Long.class);

    public final StringPath auctionUuid = createString("auctionUuid");

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isThumbnail = createBoolean("isThumbnail");

    public QAuctionImages(String variable) {
        super(AuctionImages.class, forVariable(variable));
    }

    public QAuctionImages(Path<? extends AuctionImages> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuctionImages(PathMetadata metadata) {
        super(AuctionImages.class, metadata);
    }

}

