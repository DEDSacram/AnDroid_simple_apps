package okio.internal;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.SequenceScope;
import okio.FileSystem;
import okio.Path;

@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", "Lokio/Path;"}, k = 3, mv = {1, 5, 1}, xi = 48)
@DebugMetadata(c = "okio.internal._FileSystemKt$commonListRecursively$1", f = "-FileSystem.kt", i = {0, 0}, l = {93}, m = "invokeSuspend", n = {"$this$sequence", "stack"}, s = {"L$0", "L$1"})
/* compiled from: -FileSystem.kt */
final class _FileSystemKt$commonListRecursively$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Path $dir;
    final /* synthetic */ boolean $followSymlinks;
    final /* synthetic */ FileSystem $this_commonListRecursively;
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    int label;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    _FileSystemKt$commonListRecursively$1(Path path, FileSystem fileSystem, boolean z, Continuation<? super _FileSystemKt$commonListRecursively$1> continuation) {
        super(2, continuation);
        this.$dir = path;
        this.$this_commonListRecursively = fileSystem;
        this.$followSymlinks = z;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        _FileSystemKt$commonListRecursively$1 _filesystemkt_commonlistrecursively_1 = new _FileSystemKt$commonListRecursively$1(this.$dir, this.$this_commonListRecursively, this.$followSymlinks, continuation);
        _filesystemkt_commonlistrecursively_1.L$0 = obj;
        return _filesystemkt_commonlistrecursively_1;
    }

    public final Object invoke(SequenceScope<? super Path> sequenceScope, Continuation<? super Unit> continuation) {
        return ((_FileSystemKt$commonListRecursively$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object $result) {
        SequenceScope $this$sequence;
        ArrayDeque stack;
        Iterator<Path> it;
        _FileSystemKt$commonListRecursively$1 _filesystemkt_commonlistrecursively_1;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                _filesystemkt_commonlistrecursively_1 = this;
                ArrayDeque stack2 = new ArrayDeque();
                stack2.addLast(_filesystemkt_commonlistrecursively_1.$dir);
                $this$sequence = (SequenceScope) _filesystemkt_commonlistrecursively_1.L$0;
                stack = stack2;
                it = _filesystemkt_commonlistrecursively_1.$this_commonListRecursively.list(_filesystemkt_commonlistrecursively_1.$dir).iterator();
                break;
            case 1:
                _filesystemkt_commonlistrecursively_1 = this;
                it = (Iterator) _filesystemkt_commonlistrecursively_1.L$2;
                ResultKt.throwOnFailure($result);
                stack = (ArrayDeque) _filesystemkt_commonlistrecursively_1.L$1;
                $this$sequence = (SequenceScope) _filesystemkt_commonlistrecursively_1.L$0;
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        while (it.hasNext()) {
            FileSystem fileSystem = _filesystemkt_commonlistrecursively_1.$this_commonListRecursively;
            _filesystemkt_commonlistrecursively_1.L$0 = $this$sequence;
            _filesystemkt_commonlistrecursively_1.L$1 = stack;
            _filesystemkt_commonlistrecursively_1.L$2 = it;
            _filesystemkt_commonlistrecursively_1.label = 1;
            if (_FileSystemKt.collectRecursively($this$sequence, fileSystem, stack, it.next(), _filesystemkt_commonlistrecursively_1.$followSymlinks, false, _filesystemkt_commonlistrecursively_1) == coroutine_suspended) {
                return coroutine_suspended;
            }
        }
        return Unit.INSTANCE;
    }
}
